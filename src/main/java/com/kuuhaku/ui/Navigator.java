package com.kuuhaku.ui;

import com.kuuhaku.interfaces.IMenu;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;

public abstract class Navigator {
	private static final Stack<IMenu> STACK = new Stack<>();

	public static void append(IMenu screen) {
		IMenu current = null;
		if (!STACK.isEmpty()) {
			current = STACK.peek();
		}

		STACK.push(screen);
		screen.switchTo();

		if (current != null) {
			current.dispose();
		}
	}

	public static void pop() {
		IMenu current = STACK.pop();

		if (!STACK.isEmpty()) {
			STACK.peek().switchTo();
		}

		if (current != null) {
			current.dispose();
		}
	}

	public static void popUntil(Predicate<IMenu> condition) {
		Set<IMenu> toPop = new HashSet<>();
		while (!STACK.isEmpty()) {
			IMenu next = STACK.peek();
			if (STACK.size() > 1 && !condition.test(next)) {
				toPop.add(STACK.pop());
			} else {
				next.switchTo();
				break;
			}
		}

		for (IMenu m : toPop) {
			m.dispose();
		}
	}
}
