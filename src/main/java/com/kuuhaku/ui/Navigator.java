package com.kuuhaku.ui;

import com.kuuhaku.interfaces.IMenu;

import java.util.Stack;
import java.util.function.Predicate;

public abstract class Navigator {
	private static final Stack<IMenu> STACK = new Stack<>();

	public static void append(IMenu screen) {
		if (!STACK.isEmpty()) {
			STACK.peek().dispose();
		}

		STACK.push(screen);
		screen.switchTo();
	}

	public static void pop() {
		STACK.pop().dispose();

		if (!STACK.isEmpty()) {
			STACK.peek().switchTo();
		}
	}

	public static void popUntil(Predicate<IMenu> condition) {
		while (!STACK.isEmpty()) {
			IMenu next = STACK.peek();
			if (STACK.size() > 1 && !condition.test(next)) {
				STACK.pop().dispose();
			} else {
				next.switchTo();
				break;
			}
		}
	}
}
