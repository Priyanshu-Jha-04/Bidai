package com.example.bidai

import com.example.bidai.util.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {
    @Test
    fun email() {
        assertNotNull(Validators.email(""))
        assertNotNull(Validators.email("not-an-email"))
        assertNotNull(Validators.email("a@b"))
        assertNull(Validators.email("  asha@example.com "))
    }

    @Test
    fun phoneMustBeTenDigits() {
        assertNotNull(Validators.phone(""))
        assertNotNull(Validators.phone("12345"))
        assertNotNull(Validators.phone("86574566a6"))
        assertNull(Validators.phone("8657456666"))
    }

    @Test
    fun newPasswordRequiresSixCharacters() {
        assertNotNull(Validators.newPassword(""))
        assertNotNull(Validators.newPassword("12345"))
        assertNull(Validators.newPassword("123456"))
    }

    @Test
    fun confirmPasswordMustMatch() {
        assertNotNull(Validators.confirmPassword("secret1", "secret2"))
        assertNull(Validators.confirmPassword("secret1", "secret1"))
    }

    @Test
    fun price() {
        assertNotNull(Validators.price(""))
        assertNotNull(Validators.price("abc"))
        assertNotNull(Validators.price("0"))
        assertNotNull(Validators.price("-5"))
        assertNull(Validators.price("49.99"))
        assertEquals("Price is too large", Validators.price("99999999999"))
    }

    @Test
    fun title() {
        assertNotNull(Validators.title("  "))
        assertNotNull(Validators.title("ab"))
        assertNull(Validators.title("Camera"))
    }
}
