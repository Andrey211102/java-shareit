package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Entity
@Table(name = "item_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private long id;

    @Column(name = "description", nullable = false)
    @NotBlank
    private String description;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
