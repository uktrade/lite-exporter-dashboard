package models.view;

import models.Page;
import models.enums.SortDirection;
import models.view.route.LicenceRoute;

public class OgelRegistrationListView {

  private final SortDirection reference;
  private final SortDirection licensee;
  private final SortDirection site;
  private final SortDirection registrationDate;
  private final Page<OgelRegistrationItemView> page;

  public OgelRegistrationListView(SortDirection reference, SortDirection licensee, SortDirection site, SortDirection registrationDate, Page<OgelRegistrationItemView> page) {
    this.reference = reference;
    this.licensee = licensee;
    this.site = site;
    this.registrationDate = registrationDate;
    this.page = page;
  }

  public SortDirection getReference() {
    return reference;
  }

  public SortDirection getLicensee() {
    return licensee;
  }

  public SortDirection getSite() {
    return site;
  }

  public SortDirection getRegistrationDate() {
    return registrationDate;
  }

  public Page<OgelRegistrationItemView> getPage() {
    return page;
  }

  public LicenceRoute getLicenceRoute() {
    return new LicenceRoute(reference, licensee, site, registrationDate, page.getCurrentPage());
  }

}
