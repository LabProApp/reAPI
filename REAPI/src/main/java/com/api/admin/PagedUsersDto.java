package com.api.admin;

import java.util.List;

import com.api.user.UserDto;

/**
 * Slim page wrapper for the admin user list. Avoids leaking Spring's
 * {@code Page} JSON shape (which is verbose and version-dependent).
 */
public class PagedUsersDto {

	private List<UserDto> content;
	private int page;
	private int size;
	private long total;

	public PagedUsersDto() {}

	public PagedUsersDto(List<UserDto> content, int page, int size, long total) {
		this.content = content;
		this.page = page;
		this.size = size;
		this.total = total;
	}

	public List<UserDto> getContent() { return content; }
	public void setContent(List<UserDto> content) { this.content = content; }

	public int getPage() { return page; }
	public void setPage(int page) { this.page = page; }

	public int getSize() { return size; }
	public void setSize(int size) { this.size = size; }

	public long getTotal() { return total; }
	public void setTotal(long total) { this.total = total; }
}
