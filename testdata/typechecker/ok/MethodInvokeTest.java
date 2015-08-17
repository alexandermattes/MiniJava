class MainClass{
    public static void main(String[] args){
        Blub b;
        int a;
        boolean c;
        b = new Blub();
        c = b.blah();
        a = b.bleh(4);
    }
}

class Blub {
    public boolean blah(){return false;}
    public int bleh(int a){return a;}
}
