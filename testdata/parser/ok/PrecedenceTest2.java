class PrecedenceTest{
    public static void main(String[] a){
	int x;
        x = new C().m()[2];
	}
}

class C {
    public int[] m() {
        return new int[3];
    }
}
