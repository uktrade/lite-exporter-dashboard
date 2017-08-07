package components.dao;

import com.google.inject.Inject;
import models.StatusUpdate;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

public class StatusUpdateDaoImpl implements StatusUpdateDao {

  private final DBI dbi;

  @Inject
  public StatusUpdateDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<StatusUpdate> getStatusUpdates(String appId) {
    try (final Handle handle = dbi.open()) {
      StatusUpdateJDBIDao statusUpdateJDBIDao = handle.attach(StatusUpdateJDBIDao.class);
      return statusUpdateJDBIDao.getStatusUpdates(appId);
    }
  }

  @Override
  public void insertStatusUpdate(StatusUpdate statusUpdate) {
    try (final Handle handle = dbi.open()) {
      StatusUpdateJDBIDao statusUpdateJDBIDao = handle.attach(StatusUpdateJDBIDao.class);
      statusUpdateJDBIDao.insert(statusUpdate.getAppId(), statusUpdate.getStatusType().toString(), statusUpdate.getStartTimestamp(), statusUpdate.getEndTimestamp());
    }
  }

  @Override
  public void deleteAllStatusUpdates() {
    try (final Handle handle = dbi.open()) {
      StatusUpdateJDBIDao statusUpdateJDBIDao = handle.attach(StatusUpdateJDBIDao.class);
      statusUpdateJDBIDao.truncateTable();
    }
  }

}
