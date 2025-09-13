package co.com.powerup.api.mapper;

import org.mapstruct.Mapper;

import co.com.powerup.api.dtos.response.ReportTotalizedRequestsResponse;
import co.com.powerup.model.reporttotalizedrequests.ReportTotalizedRequests;




@Mapper(componentModel = "spring")
public interface ReportTotalizedRequestsDTOMapper {

    ReportTotalizedRequestsResponse toResponse(ReportTotalizedRequests requestClient);

}