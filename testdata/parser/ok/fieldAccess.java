class fieldAccessTest{
    public static void main(String[] a){
    	B b;
    	b.c.x = 1;
    	b.getC().x = 1;
    	b.c.x = b.c.m(); 
	}
}

class B{
	C c;
	public C getC(){
		return c;
	}
}

class C {
	int x;
    public int[] m() {
        return new int[3];
    }
}