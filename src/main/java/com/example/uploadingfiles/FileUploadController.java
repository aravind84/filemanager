package com.example.uploadingfiles;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uploadingfiles.entities.FileEntity;
import com.example.uploadingfiles.repository.FileRepository;
import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

@RestController
public class FileUploadController {

	private final StorageService storageService;

	private final FileRepository fileRepository;
	
	@Autowired
	public FileUploadController(StorageService storageService, FileRepository fileRepository) {
		this.storageService = storageService;
		this.fileRepository = fileRepository;
	}

	@GetMapping("/image/all")
	public ResponseEntity<Iterable<FileEntity>> listUploadedFiles() throws IOException {
		return ResponseEntity.ok(fileRepository.findAll());
	}

	@GetMapping("/image/duplicates")
	public ResponseEntity<Iterable<FileEntity>> listDuplicateFiles() throws IOException {
		List<FileEntity> duplicateFileList = new ArrayList<FileEntity>();
		Iterator<FileEntity> iter = fileRepository.findAll().iterator();
		while (iter.hasNext()) {
			FileEntity f = iter.next();
			java.io.File file1 = new java.io.File("/tmp/upload-dir/" + f.getName());
			fileRepository.findAll().forEach(s -> {
				java.io.File file2 = new java.io.File("/tmp/upload-dir/" + s.getName());
				try {
					if (f.getId() != s.getId() && FileUtils.contentEquals(file1, file2)) {
						duplicateFileList.add(f);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}); 
		}

		return ResponseEntity.ok(duplicateFileList);
	}

	@GetMapping("/image/{id:.+}")
	public ResponseEntity<FileEntity> getFileById(@PathVariable String id) {
		return ResponseEntity.ok(fileRepository.findById(id));
	}

	@PostMapping("/image")
	public ResponseEntity<FileEntity> handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		storageService.store(file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");
		FileEntity inputfile = new FileEntity();
		inputfile.getId();
		inputfile.setName(file.getOriginalFilename());
		inputfile.setSize(file.getSize());
		inputfile.setTimestamp(Calendar.getInstance().getTime());
		fileRepository.save(inputfile);
		return ResponseEntity.ok(inputfile);
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
