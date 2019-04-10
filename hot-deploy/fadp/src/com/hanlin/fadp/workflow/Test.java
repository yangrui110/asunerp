package com.hanlin.fadp.workflow;

public class Test {
	static class T1 {
		String name = "t1";
		public void print() {
			System.out.println(name);
		}
	}

	static class T2 extends T1 {
		String name = "t2";

	/*	public void print() {
			System.out.println(super.name);
			System.out.println(name);
			super.print();
		}*/
	}

	public static void main(String[] args) {
		(new T2()).print();
	}
}
