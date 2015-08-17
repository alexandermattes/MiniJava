class MainClass{
    public static void main(String[] a){
        Blub b;
        int a;
        bolean c;
        b = new Blub();
        c = b.blah();
        a = b.bleh(4);
        b = b.bluh();
    }
}

class Blub {
    public boolean blah(){return false;}
    public int bleh(int a){return a;}
    public Blub bleh(){return new Blub();}
}
