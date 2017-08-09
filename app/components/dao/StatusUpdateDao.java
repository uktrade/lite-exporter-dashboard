package components.dao;

import models.StatusUpdate;

import java.util.List;

public interface StatusUpdateDao {

  List<StatusUpdate> getStatusUpdates(String appId);

  List<StatusUpdate> getStatusUpdates();

  void insertStatusUpdate(StatusUpdate statusUpdate);

  void deleteAllStatusUpdates();
}
