package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    @Query("select b " +
            "from Booking as b " +
            "where b.id = ?1 " +
            "and (b.item.owner.id = ?2 or b.booker.id = ?2)")
    Booking findByIdAndBookerOrOwner(Long bookingId, Long userId);

    //Поиск по bookerId
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("select b" +
            " from Booking as b " +
            "where b.status <> 'REJECTED' and " +
            "b.booker.id = ?1 and " +
            "b.start > ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerAndDatesFuture(Long bookerId, LocalDateTime currentDate);

    @Query("select b" +
            " from Booking as b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerAndDatesCurrent(Long bookerId, LocalDateTime currentDate);

    @Query("select b" +
            " from Booking as b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerAndDatesPast(Long bookerId, LocalDateTime currentDate);

    //Поиск Item ownerId
    Booking findByIdAndItem_Owner_Id(Long id, Long ownerId);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("select b" +
            " from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start > ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerAndDatesFuture(Long ownerId, LocalDateTime currentDate);

    @Query("select b" +
            " from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerAndDatesCurrent(Long ownerId, LocalDateTime currentDate);

    @Query("select b" +
            " from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerAndDatesPast(Long ownerId, LocalDateTime currentDate);

    Optional<Booking> getFirstByItem_IdAndItem_Owner_IdOrderByEnd(Long itemId, Long userId);

    Optional<Booking> getFirstByItem_IdAndAndItem_Owner_IdOrderByStartDesc(Long itemId, Long userId);
}
