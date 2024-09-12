package com.thxforservice.exelfile.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Excel1 {

    @Id
    private Long num;

    private String q;
    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

}
