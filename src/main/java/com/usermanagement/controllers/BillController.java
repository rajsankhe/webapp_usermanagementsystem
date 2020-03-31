package com.usermanagement.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;
import com.usermanagement.exceptions.FileStorageException;
import com.usermanagement.exceptions.ResourceNotFoundException;
import com.usermanagement.exceptions.ValidationException;
import com.usermanagement.models.Bill;
import com.usermanagement.models.File;
import com.usermanagement.models.User;
import com.usermanagement.services.BillService;
import com.usermanagement.services.UserService;

@RestController
public class BillController {

	@Autowired
	private BillService billService;

	@Autowired
	private UserService userService;

	@PostMapping("/bill")
	protected ResponseEntity<Bill> newBill(@Valid @RequestBody Bill bill, Authentication  authentication) throws ValidationException, ResourceNotFoundException {
		if(authentication!=null)
		{
			User user=userService.getUser(authentication.getName());
			bill.setOwnerId(user.getId());
			return new ResponseEntity<Bill>(billService.save(bill),HttpStatus.CREATED);
		}
		return new ResponseEntity<Bill>(HttpStatus.UNAUTHORIZED);
	}
	
	@GetMapping("/bills")
	protected ResponseEntity<List<Bill>> getBills(Authentication  authentication ) throws ValidationException, ResourceNotFoundException {
		if(authentication!=null)
		{
			return new ResponseEntity<List<Bill>>(billService.getBills(authentication.getName()),HttpStatus.OK);
		}
		return new ResponseEntity<List<Bill>>(HttpStatus.UNAUTHORIZED);
	}

	@GetMapping("/bill/{id}")
	protected ResponseEntity<Bill> getBill(Authentication  authentication,@PathVariable(value="id") String id ) throws ValidationException, ResourceNotFoundException {
		if(authentication!=null)
		{
			return new ResponseEntity<Bill>(billService.getBill(authentication.getName(),id),HttpStatus.OK);
		}
		return new ResponseEntity<Bill>(HttpStatus.UNAUTHORIZED);
	}
	
	@PutMapping("/bill/{id}")
	protected ResponseEntity<Bill> updateBill(@Valid @RequestBody Bill bill,Authentication  authentication,@PathVariable(value="id") String id ) throws ValidationException, ResourceNotFoundException {
		if(authentication!=null)
		{
			return new ResponseEntity<Bill>(billService.updateBill(authentication.getName(),id,bill),HttpStatus.OK);
		}
		return new ResponseEntity<Bill>(HttpStatus.UNAUTHORIZED);
	}
	
	@DeleteMapping("/bill/{id}")
	protected ResponseEntity deleteBill(Authentication  authentication,@PathVariable(value="id") UUID id ) throws ValidationException, ResourceNotFoundException, FileStorageException {
		if(authentication!=null)
		{
			billService.deleteBill(authentication.getName(),id);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity(HttpStatus.UNAUTHORIZED);
	}
	
	@RequestMapping(value ="/bill/{id}/file", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<File> attachFile(Authentication  authentication,@PathVariable(value="id") UUID billId, @RequestParam("billAttachment") MultipartFile file)
			throws ValidationException, ResourceNotFoundException, FileStorageException, NoSuchAlgorithmException, IOException {
		if(authentication!=null)
		{
			return new ResponseEntity(billService.saveAttachment(authentication.getName(), billId, file),HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.UNAUTHORIZED);
	}
	
	@GetMapping("/bill/{billId}/file/{fileId}")
	protected ResponseEntity<File> getFile(Authentication  authentication,@PathVariable(value="billId") UUID billId, @PathVariable(value="fileId") UUID fileId ) throws ValidationException, ResourceNotFoundException {
		if(authentication!=null)
		{
			return new ResponseEntity<File>(billService.getFile(authentication.getName(),billId,fileId),HttpStatus.OK);
		}
		return new ResponseEntity<File>(HttpStatus.UNAUTHORIZED);
	}
	
	@DeleteMapping("/bill/{billId}/file/{fileId}")
	protected ResponseEntity deleteFile(Authentication  authentication,@PathVariable(value="billId") UUID billId, @PathVariable(value="fileId") UUID fileId ) throws ValidationException, ResourceNotFoundException, FileStorageException {
		if(authentication!=null)
		{
			billService.deleteFile(authentication.getName(),billId,fileId);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity(HttpStatus.UNAUTHORIZED);
	}
	
	
	@GetMapping("/bills/due/{days}")
	protected ResponseEntity<List<Bill>> getDueBillsInNextDays(Authentication  authentication,@PathVariable(value="days")  int days ) throws ValidationException, ResourceNotFoundException {
		if(authentication!=null)
		{
			return new ResponseEntity<List<Bill>>(billService.getDueBillsInNextDays(authentication.getName(),days),HttpStatus.OK);
		}
		return new ResponseEntity<List<Bill>>(HttpStatus.UNAUTHORIZED);
	}
	
}