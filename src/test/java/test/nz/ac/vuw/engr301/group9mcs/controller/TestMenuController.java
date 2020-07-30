package test.nz.ac.vuw.engr301.group9mcs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import nz.ac.vuw.engr301.group9mcs.commons.PreconditionViolationException;
import nz.ac.vuw.engr301.group9mcs.controller.MenuController;

/**
 * Tests for MenuController
 * 
 * @author Claire
 */
public final class TestMenuController {
	
	/**
	 * Tests path canonicalization, including exceptions
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testPathCanonicalization()
	{
		assertEquals("some/path with spaces", MenuController.canonicalizePath("some/path with spaces"));
		assertEquals("some/path with spaces", MenuController.canonicalizePath("some/path with spaces/"));
		assertEquals("some/path with spaces", MenuController.canonicalizePath("/some/path with spaces"));
		assertEquals("some/path with spaces", MenuController.canonicalizePath("some/path with spaces/"));
		assertEquals("some/path with spaces", MenuController.canonicalizePath("/some/PaTh WiTh SpAcES/"));
		
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("some/invalid/path"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("some/"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("/invalid"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("/"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("//"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("///"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("donut/some/invalid/path"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("some//path"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("//path"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("some//"); });
		assertThrows(PreconditionViolationException.class, () -> { MenuController.canonicalizePath("some///"); });
	}
	
}
