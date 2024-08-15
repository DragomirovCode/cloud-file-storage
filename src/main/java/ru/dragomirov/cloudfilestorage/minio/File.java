package ru.dragomirov.cloudfilestorage.minio;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "minio_path")
    private String minioPath;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

}
