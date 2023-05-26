package com.team11.shareoffice.email.entity;


import com.team11.shareoffice.email.dto.EmailRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor
public class Email {
    @Id
    @Column(nullable = false, unique = true)
    private String email;
    private String code;
    @ColumnDefault("false")
    private boolean checked;

    private Email(String email){
        this.email = email;
    }
    private void setCode(String code) {
        this.code = code;
    }

    private void setChecked (boolean isChecked) {
        this.checked = isChecked;
    }
    public static Email saveEmail(EmailRequestDto requestDto) {
        return new Email(requestDto.getEmail());
    }

    public void updateCode( String code) {
        this.setCode(code);
    }

    public void updateChecked(Boolean isChecked){this.setChecked(isChecked);}



}
