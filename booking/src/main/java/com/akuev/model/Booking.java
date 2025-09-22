package com.akuev.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Booking")
public class Booking {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;*/
    private UUID userId;

    /*
    @ManyToOne
    @JoinColumn(name = "session_id", referencedColumnName = "session_id")
    private MovieSession session;*/
    private Long sessionId;

    @Column(name = "seats", columnDefinition = "TEXT[]")
    private Set<String> bookedSeats;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;
}
