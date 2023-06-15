package com.team11.shareoffice.reservation.util;

import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ReservationScheduler {

    @Autowired
    private ReservationRepository reservationRepository;

    @Transactional
    @Scheduled(cron = "59 59 23 * * *")
    public void test(){
        List<Reservation> allReservations = reservationRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        for(Reservation reservation : allReservations){
            String reservationEndDate = reservation.getEndDate().format(formatter);
            int comparisonOfDate = today.compareTo(reservationEndDate);
           if(reservationEndDate.equals(today) || comparisonOfDate > 0) //예약 종료일이 오늘 보다 전이 거나 같을 경우
           {
              reservation.setFinished(true);

           }
        }
    }
}

