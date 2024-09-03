package com.luv2code.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

	@Id
	private Integer id;

	@Column(value = "author")
	private String author;

	@Column(value = "description")
	private String description;

	@Column(value = "cover_image")
	private String coverImage;

	@Column(value = "title")
	private String title;

	@Column(value = "publication_year")
	private Integer publicationYear;

}
