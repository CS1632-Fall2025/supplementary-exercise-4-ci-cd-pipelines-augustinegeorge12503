package edu.pitt.cs;

import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List; 

public interface RentACat {
	public static RentACat createInstance(InstanceType type) {
		switch (type) {
			case IMPL:
				return new RentACatImpl();
			case BUGGY:
				return new RentACatBuggy();
			case SOLUTION:
				return new RentACatSolution();
			case MOCK:
				// Mock that emulates RentACatImpl behavior
				RentACat mock = Mockito.mock(RentACat.class);

				List<Cat> cats = new ArrayList<>();
				final String nl = System.lineSeparator();

				// addCat
				doAnswer(inv -> {
					Cat c = (Cat) inv.getArgument(0);
					cats.add(c);
					return null;
				}).when(mock).addCat(any(Cat.class));

				// listCats
				when(mock.listCats()).thenAnswer(inv -> {
					StringBuilder sb = new StringBuilder();
					for (Cat c : cats) {
						if (!c.getRented()) {
							sb.append(c.toString()).append(nl);
						}
					}
					return sb.toString();
				});

				// Helper to find cat by id
				java.util.function.IntFunction<Cat> findById = (int id) -> {
					for (Cat c : cats) {
						if (c.getId() == id) return c;
					}
					return null;
				};

				// rentCat
				when(mock.rentCat(anyInt())).thenAnswer(inv -> {
					int id = inv.getArgument(0);
					Cat c = findById.apply(id);
					if (c == null) {
						System.out.println("Invalid cat ID.");
						return false;
					}
					if (!c.getRented()) {
						c.rentCat();
						System.out.println(c.getName() + " has been rented.");
						return true;
					} else {
						System.out.println("Sorry, " + c.getName() + " is not here!");
						return false;
					}
				});

				// returnCat
				when(mock.returnCat(anyInt())).thenAnswer(inv -> {
					int id = inv.getArgument(0);
					Cat c = findById.apply(id);
					if (c == null) {
						System.out.println("Invalid cat ID.");
						return false;
					}
					if (c.getRented()) {
						c.returnCat();
						System.out.println("Welcome back, " + c.getName() + "!");
						return true;
					} else {
						System.out.println(c.getName() + " is already here!");
						return false;
					}
				});

				// renameCat
				when(mock.renameCat(anyInt(), anyString())).thenAnswer(inv -> {
					int id = inv.getArgument(0);
					String name = inv.getArgument(1);
					Cat c = findById.apply(id);
					if (c == null) {
						System.out.println("Invalid cat ID.");
						return false;
					}
					c.renameCat(name);
					return true;
				});

				return mock;
			default:
				assert (false);
				return null;
		}
	}

	// WARNING: You are not allowed to change any part of the interface.
	// That means you cannot add any method nor modify any of these methods.

	public boolean returnCat(int id);

	public boolean rentCat(int id);

	public boolean renameCat(int id, String name);

	public String listCats();

	public void addCat(Cat c);
}
