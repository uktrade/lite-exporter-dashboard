package models.view.form;

import play.data.validation.Constraints;

public class AmendApplicationForm {

  public String delete;

  @Constraints.Required(message = "Sort of change is required.")
  public String action;

  @Constraints.Required(message = "Message is required.")
  public String message;

}
