class PrecedenceTest{
    public static void main(String[] a){
	int x;
        x = new C().m();
	}
}

class C {
    public int m() {
        return 0;
    }
}
