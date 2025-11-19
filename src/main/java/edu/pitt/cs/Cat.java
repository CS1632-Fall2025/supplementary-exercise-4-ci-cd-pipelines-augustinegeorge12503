package edu.pitt.cs;

import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference; 

public interface Cat {
	public static Cat createInstance(InstanceType type, int id, String name) {
		switch (type) {
			case IMPL:
				return new CatImpl(id, name);
			case BUGGY:
				return new CatBuggy(id, name);
			case SOLUTION:
				return new CatSolution(id, name);
			case MOCK:
                // Mock that emulates the real object
                Cat mock = Mockito.mock(Cat.class);

                AtomicReference<String> nm = new AtomicReference<>(name);
                AtomicBoolean rented = new AtomicBoolean(false);

                // Fixed ID
                when(mock.getId()).thenReturn(id);

                // Name get/set
                when(mock.getName()).thenAnswer(inv -> nm.get());
                doAnswer(inv -> {
                    nm.set((String) inv.getArgument(0));
                    return null;
                }).when(mock).renameCat(anyString());

                // Rented get/set
                when(mock.getRented()).thenAnswer(inv -> rented.get());
                doAnswer(inv -> {
                    rented.set(true);
                    return null;
                }).when(mock).rentCat();
                doAnswer(inv -> {
                    rented.set(false);
                    return null;
                }).when(mock).returnCat();

                when(mock.toString()).thenAnswer(inv -> "ID " + id + ". " + nm.get());

                return mock;
			default:
				assert(false);
				return null;
		}
	}

	// WARNING: You are not allowed to change any part of the interface.
	// That means you cannot add any method nor modify any of these methods.
	
	public void rentCat();

	public void returnCat();

	public void renameCat(String name);

	public String getName();

	public int getId();

	public boolean getRented();

	public String toString();
}
