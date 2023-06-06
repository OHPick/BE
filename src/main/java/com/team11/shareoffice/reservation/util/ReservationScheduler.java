package com.team11.shareoffice.reservation.util;

import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ReservationScheduler {

    @Autowired
    private ReservationRepository reservationRepository;

    @Scheduled(cron = "0 0 18 * * *")
    public void test(){
        List<Reservation> allReservations = reservationRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        for(Reservation reservation : allReservations){
            String reservationEndDate = reservation.getEndDate().format(formatter);
           if(reservationEndDate.equals(today))
           {
               System.out.println("NOW");
           }
        }

    }
}
