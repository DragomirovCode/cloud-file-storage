package ru.dragomirov.cloudfilestorage.minio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String minioPath;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

}
