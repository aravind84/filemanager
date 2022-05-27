package com.example.uploadingfiles.repository;

import org.springframework.data.repository.CrudRepository;


import com.example.uploadingfiles.entities.FileEntity;

public interface FileRepository extends CrudRepository<FileEntity, Long> {

	FileEntity findById(String id);
}
