package com.demo.app.mapper;

import com.demo.app.entity.Reservation;
import com.demo.app.model.MakeReservationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

}
