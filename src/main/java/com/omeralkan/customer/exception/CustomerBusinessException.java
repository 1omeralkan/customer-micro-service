package com.omeralkan.customer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// Neden @Data kullanmadık da sadece @Getter kullandık?
// Çünkü bir hata fırlatıldıktan sonra içindeki değerler sonradan değiştirilemez (Immutable olmalıdır).
@Getter
public class CustomerBusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    // DİKKAT: Yapıcı metoda (Constructor) "String message" parametresi KOYMADIK!
    // Hardcoded metin tuzağına düşmemek için sadece Hata Kodunu (Örn: "CUST-404") alıyoruz.
    // Mesajı bulma işini bir sonraki adımda yazacağımız "Radar" yapacak.
    public CustomerBusinessException(String errorCode, HttpStatus httpStatus) {
        // Parent sınıfa (RuntimeException) sadece hata kodunu gönderiyoruz ki arka planda loglanabilsin.
        super(errorCode);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}