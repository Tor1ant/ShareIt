package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, PagingAndSortingRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker booker " +
            "WHERE b.id = :bookingId " +
            "AND (booker.id = :userId OR i.owner.id = :userId)")
    Optional<Booking> findByIdWithItemAndBooker(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    Optional<Booking> findBookingByIdAndItemOwnerId(Long bookingId, Long ownerId);

    Optional<List<Booking>> findBookingsByBookerId(Long bookerId, Pageable pageable);

    Optional<List<Booking>> findBookingsByItemOwnerIdOrderByEndDesc(Long ownerId, Pageable sortedByEndDesc);

    Optional<List<Booking>> findBookingsByStatusAndBookerIdOrderByEndDesc(BookingStatus bookingStatus, Long bookerId,
                                                                          Pageable pageable);

    Optional<List<Booking>> findBookingsByStatusAndItemOwnerIdOrderByEndDesc(BookingStatus bookingStatus,
                                                                             Long bookerId, Pageable sortedByEndDesc);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where booker.id = :bookerId and b.start <= current_timestamp  and  b.end >= current_timestamp "
            + "order by b.end desc ")
    Optional<List<Booking>> findAllCurrentByUserIdAndSortByDesc(@Param("bookerId") Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.owner.id = :ownerId and b.start <= current_timestamp  and  b.end >= current_timestamp "
            + "order by b.end desc ")
    Optional<List<Booking>> findAllCurrentByOwnerIdAndSortByDesc(@Param("ownerId") Long ownerId, Pageable sortedByEndDesc);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where booker.id = :bookerId and b.end < current_timestamp "
            + "order by b.end desc")
    Optional<List<Booking>> findAllPastByUserIdAndSortByDesc(@Param("bookerId") Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.owner.id = :ownerId and b.end < current_timestamp "
            + "order by b.end desc")
    Optional<List<Booking>> findAllPastByOwnerIdAndSortByDesc(@Param("ownerId") Long ownerId, Pageable sortedByEndDesc);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where booker.id = :bookerId and b.start > current_timestamp "
            + "order by b.end desc")
    Optional<List<Booking>> findAllFutureByUserIdAndSortByDesc(@Param("bookerId") Long bookerId, Pageable pageable);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.owner.id = :ownerId and b.start > current_timestamp "
            + "order by b.end desc")
    Optional<List<Booking>> findAllFutureByOwnerIdAndSortByDesc(@Param("ownerId") Long ownerId, Pageable sortedByEndDesc);

    Optional<List<Booking>> findBookingsByItemIdOrderByEndDesc(Long itemId);

    @Query("select b "
            + "from Booking as b "
            + "join fetch b.booker as booker "
            + "join fetch b.item as i "
            + "where i.id in (:itemsId) "
            + "order by b.end desc")
    List<Booking> findBookingsByItemId(@Param("itemsId") List<Long> itemsId);

    @Query("select b "
            + "from Booking as b "
            + "join  b.item as i "
            + "join  b.booker as booker"
            + " where booker.id = :userId and i.id = :itemId and b.status = 'APPROVED' and  b.end < current_timestamp")
    List<Booking> findBookingByBookerIdAndItemIdAndStatusApproved(@Param("userId") Long bookerId,
                                                                  @Param("itemId") Long itemId);

}
