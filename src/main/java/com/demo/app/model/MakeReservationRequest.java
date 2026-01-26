package com.demo.app.model;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MakeReservationRequest {

    private Long passengerId;

    private Long seatId;

    private Long flightId;

}
