package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    @Query("select b " +
            "from Booking as b " +
            "where b.id = ?1 " +
            "and (b.item.owner.id = ?2 or b.booker.id = ?2)")
    Booking findByIdAndBookerOrOwner(Long bookingId, Long userId);

    //Поиск по bookerId
    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, PageRequest request);

    Page<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, PageRequest request);

    @Query("select b" +
            " from Booking as b " +
            "where b.status <> 'REJECTED' and " +
            "b.booker.id = ?1 and " +
            "b.start > ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerAndDatesFuture(Long bookerId, LocalDateTime currentDate, PageRequest request);

    @Query("select b" +
            " from Booking as b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerAndDatesCurrent(Long bookerId, LocalDateTime currentDate, PageRequest request);

    @Query("select b" +
            " from Booking as b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerAndDatesPast(Long bookerId, LocalDateTime currentDate, PageRequest request);

    //Поиск Item ownerId
    Booking findByIdAndItem_Owner_Id(Long id, Long ownerId);

    Page<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId, PageRequest request);

    Page<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status,
                                                                  PageRequest request);

    @Query("select b" +
            " from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start > ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerAndDatesFuture(Long ownerId, LocalDateTime currentDate, PageRequest request);

    @Query("select b" +
            " from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerAndDatesCurrent(Long ownerId, LocalDateTime currentDate, PageRequest request);

    @Query("select b" +
            " from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerAndDatesPast(Long ownerId, LocalDateTime currentDate, PageRequest request);

    Optional<Booking> getFirstByItem_IdAndItem_Owner_IdOrderByEnd(Long itemId, Long userId);

    Optional<Booking> getFirstByItem_IdAndAndItem_Owner_IdOrderByStartDesc(Long itemId, Long userId);
}
