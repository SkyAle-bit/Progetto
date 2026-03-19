package com.project.tesi.exception.user;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando l'amministratore tenta di eliminare se stesso.
 */
public class AdminSelfDeletionNotAllowedException extends BaseException {

	public AdminSelfDeletionNotAllowedException() {
		super("L'amministratore non puo eliminare se stesso.", HttpStatus.CONFLICT);
	}
}

