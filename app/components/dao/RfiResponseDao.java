package components.dao;

import models.RfiResponse;

import java.util.List;

public interface RfiResponseDao {

  List<RfiResponse> getRfiResponses(List<String> rfiIds);

  RfiResponse getRfiResponse(String rfiId);

  void insertRfiResponse(RfiResponse rfiResponse);

  void deleteAllRfiResponses();

}