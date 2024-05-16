package ru.victoriaknyazeva.otus.webserver.application;

import java.util.Objects;
import java.util.UUID;

public record Item(UUID id,String title,int price){

	public Item(UUID id, String title, int price) {
		if (id==null) {
			this.id = UUID.randomUUID();
		} else {
			this.id = id;
		}
		this.title =title;
		this.price = price;
	}

	public Item(String title, int price) {
		this(UUID.randomUUID(), title, price);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Item item)) return false;
        return Objects.equals(id, item.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
};
