package com.usermanagement.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.usermanagement.models.File;

public interface FileReposiory extends CrudRepository<File, UUID> {

}
