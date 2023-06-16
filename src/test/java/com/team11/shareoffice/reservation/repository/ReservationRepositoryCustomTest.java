package com.team11.shareoffice.reservation.repository;

import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.reservation.entity.Reservation;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Ensure that the test runs within a transaction and rolls back after the test
class ReservationRepositoryCustomTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    void concurrentFindAllByPostReservedAndNotFinishedTest() throws InterruptedException, ExecutionException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Member member = new Member();
        Post post = new Post(1L,member,"title","content","location",100,10,"operatingTime","contentDetails","amenities",0,"postImage",false);
        LocalDate startDate = LocalDate.of(2023, 6, 20);
        LocalDate endDate = LocalDate.of(2023, 6, 22);
        List<Reservation> reservationList = new ArrayList<>();
        AtomicBoolean errorCheck = new AtomicBoolean(false);

        // Insert some reservations into the database
        // Assuming you have some existing reservations for the specified date range
        // Modify this part according to your database setup
        for (int i = 0; i < 10; i++) {
            Reservation reservation = new Reservation();
            reservation.setPost(post);
            reservation.setMember(member);
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            // Set other necessary fields for the reservation
            reservationList.add(reservation);
        }
        reservationRepository.saveAll(reservationList);

        // Set up the behavior of the reservationRepository mock
        Mockito.when(reservationRepository.findAllByPostReservedAndNotFinished(Mockito.eq(post), Mockito.eq(startDate), Mockito.eq(endDate)))
                .thenReturn(reservationList);

        // Concurrently execute the findAllByPostReservedAndNotFinished method
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // Acquire a pessimistic write lock on the reservations
                    List<Reservation> result = reservationRepository.findAllByPostReservedAndNotFinished(post, startDate, endDate);

                    // Check for duplicate reservations
                    Set<Long> reservationIds = new HashSet<>();
                    for (Reservation reservation : result) {
                        if (reservationIds.contains(reservation.getId())) {
                            errorCheck.set(true);

                        }
                        reservationIds.add(reservation.getId());
                    }
                } finally {
                    latch.countDown();
                }
            });

        }
        latch.await();

        assertTrue(errorCheck.get());
        System.out.println(errorCheck);

    }
}
