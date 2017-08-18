package components.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.dao.ApplicationDao;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import components.dao.StatusUpdateDao;
import models.Application;
import models.Rfi;
import models.RfiResponse;
import models.StatusUpdate;
import models.enums.SortDirection;
import models.enums.StatusType;
import models.view.ApplicationItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.customer.api.view.CustomerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationItemViewServiceImpl implements ApplicationItemViewService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationItemViewServiceImpl.class);
  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

  private static final Map<SortDirection, Comparator<ApplicationItemView>> DATE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<ApplicationItemView>> STATUS_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<ApplicationItemView>> CREATED_BY_COMPARATORS = new EnumMap<>(SortDirection.class);

  static {
    Comparator<ApplicationItemView> dateComparator = Comparator.comparing(ApplicationItemView::getDateSubmittedTimestamp);
    DATE_COMPARATORS.put(SortDirection.ASC, dateComparator);
    DATE_COMPARATORS.put(SortDirection.DESC, dateComparator.reversed());
    Comparator<ApplicationItemView> statusComparator = Comparator.comparing(ApplicationItemView::getApplicationStatusTimestamp);
    STATUS_COMPARATORS.put(SortDirection.ASC, statusComparator);
    STATUS_COMPARATORS.put(SortDirection.DESC, statusComparator.reversed());
    Comparator<ApplicationItemView> createdByComparator = Comparator.comparing(ApplicationItemView::getApplicantReference);
    CREATED_BY_COMPARATORS.put(SortDirection.ASC, createdByComparator);
    CREATED_BY_COMPARATORS.put(SortDirection.DESC, createdByComparator.reversed());
  }

  private final ApplicationDao applicationDao;
  private final StatusUpdateDao statusUpdateDao;
  private final TimeFormatService timeFormatService;
  private final StatusService statusService;
  private final RfiDao rfiDao;
  private final RfiResponseDao rfiResponseDao;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final CustomerServiceClient customerServiceClient;

  @Inject
  public ApplicationItemViewServiceImpl(ApplicationDao applicationDao,
                                        StatusUpdateDao statusUpdateDao,
                                        TimeFormatService timeFormatService,
                                        StatusService statusService,
                                        RfiDao rfiDao,
                                        RfiResponseDao rfiResponseDao,
                                        ApplicationSummaryViewService applicationSummaryViewService,
                                        CustomerServiceClient customerServiceClient) {
    this.applicationDao = applicationDao;
    this.statusUpdateDao = statusUpdateDao;
    this.timeFormatService = timeFormatService;
    this.statusService = statusService;
    this.rfiDao = rfiDao;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.customerServiceClient = customerServiceClient;
  }

  @Override
  public List<ApplicationItemView> getApplicationItemViews(String userId, SortDirection dateSortDirection, SortDirection statusSortDirection, SortDirection createdBySortDirection) {
    List<CustomerView> customerViews = customerServiceClient.getCustomers(userId);

    Map<String, String> customerIdToCompanyName = customerViews.stream()
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<String> customerIds = new ArrayList<>(customerIdToCompanyName.keySet());

    List<Application> applications = applicationDao.getApplications(customerIds);
    List<String> appIds = applications.stream().map(Application::getAppId).collect(Collectors.toList());

    Multimap<String, StatusUpdate> appIdToStatusUpdateMap = HashMultimap.create();
    statusUpdateDao.getStatusUpdates(appIds).forEach(statusUpdate -> appIdToStatusUpdateMap.put(statusUpdate.getAppId(), statusUpdate));

    Map<String, String> appIdToOpenRfiIdMap = getAppIdToOpenRfiIdMap(appIds);

    List<ApplicationItemView> applicationItemViews = applications.stream()
        .map(application -> {
          String companyName = customerIdToCompanyName.get(application.getCompanyId());
          String appId = application.getAppId();
          Collection<StatusUpdate> statusUpdates = appIdToStatusUpdateMap.get(appId);
          String openRfiId = appIdToOpenRfiIdMap.get(appId);
          return getApplicationItemView(application, companyName, statusUpdates, openRfiId);
        })
        .collect(Collectors.toList());

    sort(applicationItemViews, dateSortDirection, statusSortDirection, createdBySortDirection);

    return applicationItemViews;
  }

  private void sort(List<ApplicationItemView> applicationItemViews, SortDirection dateSortDirection, SortDirection statusSortDirection, SortDirection createdBySortDirection) {
    if (dateSortDirection != null) {
      applicationItemViews.sort(DATE_COMPARATORS.get(dateSortDirection));
    }
    if (statusSortDirection != null) {
      applicationItemViews.sort(STATUS_COMPARATORS.get(statusSortDirection));
    }
    if (createdBySortDirection != null) {
      applicationItemViews.sort(CREATED_BY_COMPARATORS.get(createdBySortDirection));
    }
  }

  private ApplicationItemView getApplicationItemView(Application application, String companyName, Collection<StatusUpdate> statusUpdates, String openRfiId) {

    long dateSubmittedTimestamp = getDateSubmittedTimestamp(statusUpdates);
    String dateSubmitted = getDateSubmitted(dateSubmittedTimestamp);

    StatusType statusType = null;
    String applicationStatus = "";
    String applicationStatusDate = "";
    long applicationStatusTimestamp = 0;

    StatusUpdate maxStatusUpdate = applicationSummaryViewService.getMaxStatusUpdate(statusUpdates).orElse(null);
    if (maxStatusUpdate != null) {
      applicationStatus = statusService.getStatus(maxStatusUpdate.getStatusType());
      Long maxTimestamp = maxStatusUpdate.getStartTimestamp();
      if (maxTimestamp != null) {
        applicationStatusDate = String.format("Since: %s", timeFormatService.formatDateWithSlashes(maxTimestamp));
        applicationStatusTimestamp = maxTimestamp;
      }
      statusType = maxStatusUpdate.getStatusType();
    }

    String destination = applicationSummaryViewService.getDestination(application);
    return new ApplicationItemView(application.getAppId(),
        application.getCompanyId(),
        companyName,
        application.getApplicantReference(),
        dateSubmittedTimestamp,
        dateSubmitted,
        application.getCaseReference(),
        statusType,
        applicationStatus,
        applicationStatusDate,
        applicationStatusTimestamp,
        destination,
        openRfiId
    );
  }

  private Map<String, String> getAppIdToOpenRfiIdMap(List<String> appIds) {
    List<Rfi> rfiList = rfiDao.getRfiList(appIds);
    Multimap<String, Rfi> appIdToRfi = HashMultimap.create();
    rfiList.forEach(rfi -> appIdToRfi.put(rfi.getAppId(), rfi));

    List<String> rfiIds = rfiList.stream()
        .map(Rfi::getRfiId)
        .collect(Collectors.toList());
    Set<String> answeredRfiIds = rfiResponseDao.getRfiResponses(rfiIds).stream()
        .map(RfiResponse::getRfiId)
        .collect(Collectors.toSet());

    Map<String, String> appIdToOpenRfiIdMap = new HashMap<>();
    appIdToRfi.asMap().forEach((appId, rfiCollection) -> {
      String openRfiId = getOpenRfiId(rfiCollection, answeredRfiIds);
      appIdToOpenRfiIdMap.put(appId, openRfiId);
    });
    return appIdToOpenRfiIdMap;
  }

  private String getOpenRfiId(Collection<Rfi> rfiList, Set<String> answeredRfiIds) {
    return rfiList.stream()
        .filter(rfi -> !answeredRfiIds.contains(rfi.getRfiId()))
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp).reversed())
        .map(Rfi::getRfiId)
        .findFirst()
        .orElse(null);
  }

  private long getDateSubmittedTimestamp(Collection<StatusUpdate> statusUpdates) {
    Optional<StatusUpdate> statusUpdate = statusUpdates.stream().filter(su -> su.getStatusType() == StatusType.SUBMITTED).findAny();
    if (!statusUpdate.isPresent()) {
      statusUpdate = statusUpdates.stream().filter(su -> su.getStatusType() == StatusType.DRAFT).findAny();
    }
    if (statusUpdate.isPresent() && statusUpdate.get().getStartTimestamp() != null) {
      return statusUpdate.get().getStartTimestamp();
    } else {
      return 0;
    }
  }

  private String getDateSubmitted(long dateSubmittedTimestamp) {
    if (dateSubmittedTimestamp == 0) {
      return "";
    } else {
      return timeFormatService.formatDateWithSlashes(dateSubmittedTimestamp);
    }
  }

}
