import java.io.*;
import java.util.*;

class Node {

    Node parent;
    String nama;
    long suaraX;
    long suaraY;

    boolean noted = false;

    Node (Node parent, String nama) {
        this.parent = parent;
        this.nama = nama;
    }

    public void addParent(Node parent) {
        this.parent = parent;
    }
    
    public void setSuara(long suaraX, long suaraY) {
        this.suaraX = suaraX;
        this.suaraY = suaraY;
    }

    public void tambahSuara(long suaraX, long suaraY) {
        // System.out.println("Before " + this.nama + " " + this.noted + " " + this.suaraX + " " + this.suaraY);
        if (noted) {
            PemiluDuar.root = AVL_Tree.deleteNode(PemiluDuar.root, Math.abs(this.suaraX - this.suaraY));
            noted = false;
        }

        this.suaraX += suaraX;
        this.suaraY += suaraY;
        // System.out.println(this.nama + " " + this.noted + " " + this.suaraX + " " + this.suaraY);
        PemiluDuar.root = AVL_Tree.insert(PemiluDuar.root, Math.abs(this.suaraX - this.suaraY));
        noted = true;
        
        try {
            this.parent.tambahSuara(suaraX, suaraY);
        } catch (NullPointerException e) {
            return;
        }

    }

    public void anulirSuara(long suaraX, long suaraY) {
        // System.out.println(this.nama + " " + this.noted + " " + this.suaraX + " " + this.suaraY);
        if (noted) {
            PemiluDuar.root = AVL_Tree.deleteNode(PemiluDuar.root, Math.abs(this.suaraX - this.suaraY));
            noted = false;
        }

        this.suaraX -= suaraX;
        this.suaraY -= suaraY;
        // System.out.println(this.nama + " " + this.noted + " " + this.suaraX + " " + this.suaraY);
        PemiluDuar.root = AVL_Tree.insert(PemiluDuar.root, Math.abs(this.suaraX - this.suaraY));
        noted = true;

        try {
            this.parent.anulirSuara(suaraX, suaraY);
        } catch (NullPointerException e) {
            return;
        }

    }

}

class AVL_Node {
    long key, count;
    long height;
    AVL_Node left, right;

    AVL_Node(long selisih) {
        key = selisih;
        height = 1;
        left = null;
        right = null;
        count = 1;
    }
}

class AVL_Tree {
    AVL_Node root;

    static long height(AVL_Node node) {
        if (node == null)
            return 0;
        return node.height;
    }

    static long max(long a, long b) {
        return (a > b) ? a : b;
    }

    static AVL_Node rotateRight(AVL_Node y) {
        AVL_Node x = y.left;
        AVL_Node temp = x.right;

        x.right = y;
        y.left = temp;

        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        return x;
    }

    static AVL_Node rotateLeft(AVL_Node x) {
        AVL_Node y = x.right;
        AVL_Node temp = y.left;

        y.left = x;
        x.right = temp;

        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        return y;
    }

    static long getBalance(AVL_Node node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    static AVL_Node insert(AVL_Node node, long key) {
        // System.out.println("Masuk insert hehe " + node + " " + key);
        if (node == null)
            return (new AVL_Node(key));

        if (key == node.key) {
            (node.count)++;
            return node;
        }

        if (key < node.key)
            node.left = insert(node.left, key);
        else
            node.right = insert(node.right, key);

        node.height = max(height(node.left), height(node.right)) + 1;

        long balance = getBalance(node);

        if (balance > 1 && key < node.left.key)
            return rotateRight(node);

        if (balance < -1 && key > node.right.key)
            return rotateLeft(node);

        if (balance > 1 && key > node.left.key) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && key < node.right.key) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;

    }

    static AVL_Node minValueNode(AVL_Node node) {
        AVL_Node current = node;

        while (current.left != null)
            current = current.left;

        return current;
    }

    static AVL_Node deleteNode(AVL_Node root, long key) {
        if (root == null)
            return root;

        if (key < root.key)
            root.left = deleteNode(root.left, key);

        else if (key > root.key)
            root.right = deleteNode(root.right, key);

        else {
            if (root.count > 1) {
                root.count--;
                return root;
            }

            if ((root.left == null) || (root.right == null)) {
                AVL_Node temp = root.left != null ? root.left : root.right;

                if (temp == null) {
                    temp = root;
                    root = null;
                } else
                    root = temp;

            } else {
                AVL_Node temp = minValueNode(root.right);

                root.key = temp.key;
                root.count = temp.count;
                temp.count = 1;

                root.right = deleteNode(root.right, temp.key);
            }

        }

        if (root == null)
            return root;

        root.height = max(height(root.left), height(root.right));

        long balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0)
            return rotateRight(root);

        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = rotateLeft(root.left);
            return rotateRight(root);
        }

        if (balance < -1 && getBalance(root.right) <= 0)
            return rotateLeft(root);

        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rotateRight(root.right);
            return rotateLeft(root);
        }

        return root;
    }

    static void preOrder(AVL_Node root) 
    { 
        // System.out.println(root);
        if (root != null) { 
            System.out.printf("%d(%d) ", root.key, root.count); 
            preOrder(root.left); 
            preOrder(root.right); 
        } 
        // System.out.println();
    }

    static long getDenganSelisih(AVL_Node root, long minimal) {
        long ans = 0;
        if (root.key >= minimal) {
            ans += root.count;
        }

        if (root.left != null) {
            ans += getDenganSelisih(root.left, minimal);
        }

        if (root.right != null) {
            ans += getDenganSelisih(root.right, minimal);
        }

        return ans;
    }

}

public class PemiluDuar {
    private static InputReader in;
    private static PrintWriter out;
    // private static int N;
    static HashMap<String, Node> wilayah = new HashMap<>();
    // static HashMap<String, Node> subWilayah = new HashMap<>();
    static ArrayList<String> provinsi = new ArrayList<>();

    static AVL_Node root = null;

    public static void main(String[] args) throws IOException {
        in = new InputReader(System.in);
        out = new PrintWriter(System.out);

        String firstDonat = in.next();
        String secondDonat = in.next();

        long N = in.nextInt();

        // AVL_Node root = null;

        for (int i = 0; i < N; i++) {
            if (i == 0) {

                String namaWilayah = in.next();
                Node nodeWilayah = new Node(null, namaWilayah);

                nodeWilayah.setSuara(0, 0);
                wilayah.put(namaWilayah, nodeWilayah);

                long Z = in.nextInt();

                for (int j = 0; j < Z; j++) {

                    String namaSubWilayah = in.next();
                    Node nodeSubWilayah = new Node(nodeWilayah, namaSubWilayah);

                    wilayah.put(namaSubWilayah, nodeSubWilayah);
                    provinsi.add(namaSubWilayah);

                }

            } else {

                String namaWilayah = in.next();
                long Z = in.nextInt();

                for (int j = 0; j < Z; j++) {
                    String namaSubWilayah = in.next();
                    Node nodeSubWilayah = new Node(wilayah.get(namaWilayah), namaSubWilayah);
                    wilayah.put(namaSubWilayah, nodeSubWilayah);
                }

            }

        }

        long M = in.nextInt();
        for (int i = 0; i < M; i++) {

            String operasi = in.next();
            if (operasi.equals("TAMBAH")) {

                String wilayahSuara = in.next();
                long suaraX = in.nextInt();
                long suaraY = in.nextInt();

                // System.out.println("cek " + wilayah.get(wilayahSuara).noted);

                // if (wilayah.get(wilayahSuara).noted) {
                //     root = AVL_Tree.deleteNode(root,
                //             Math.abs(wilayah.get(wilayahSuara).suaraX - wilayah.get(wilayahSuara).suaraY));
                //     wilayah.get(wilayahSuara).noted = false;
                //     // System.out.println("Masuk");
                // }

                wilayah.get(wilayahSuara).tambahSuara(suaraX, suaraY);

                // root = AVL_Tree.insert(root, 
                //         Math.abs(wilayah.get(wilayahSuara).suaraX - wilayah.get(wilayahSuara).suaraY));
                // wilayah.get(wilayahSuara).noted = true;

                // for (String str: wilayah.keySet()) {
                //     out.println(str + " " + wilayah.get(str).suaraX + " " + wilayah.get(str).suaraY);
                // }
                // System.out.println("cek lagi " + wilayah.get(wilayahSuara).noted);
                // AVL_Tree.preOrder(root);
                // System.out.println();
                // BTreePrinter.printNode(root);

            } else if (operasi.equals("ANULIR")) {

                String wilayahSuara = in.next();
                long suaraX = in.nextInt();
                long suaraY = in.nextInt();

                // if (wilayah.get(wilayahSuara).noted) {
                //     root = AVL_Tree.deleteNode(root, 
                //             Math.abs(wilayah.get(wilayahSuara).suaraX - wilayah.get(wilayahSuara).suaraY));
                //     wilayah.get(wilayahSuara).noted = false;
                // }

                wilayah.get(wilayahSuara).anulirSuara(suaraX, suaraY);

                // root = AVL_Tree.insert(root, 
                //         Math.abs(wilayah.get(wilayahSuara).suaraX - wilayah.get(wilayahSuara).suaraY));
                // wilayah.get(wilayahSuara).noted = true;

                // for (String str: wilayah.keySet()) {
                //     out.println(str + " " + wilayah.get(str).suaraX + " " + wilayah.get(str).suaraY);
                // }
                // AVL_Tree.preOrder(root);
                // System.out.println();
                // BTreePrinter.printNode(root);

            } else if (operasi.equals("CEK_SUARA")) {

                String cekWilayah = in.next();
                out.println(wilayah.get(cekWilayah).suaraX + " " + wilayah.get(cekWilayah).suaraY);

            } else if (operasi.equals("WILAYAH_MENANG")) {

                String donat = in.next();
                long menang = 0;

                if (donat.equals(firstDonat)) {
                    
                    for (String str: wilayah.keySet()) {
                        if (wilayah.get(str).suaraX > wilayah.get(str).suaraY) {
                            menang++;
                        }
                    }

                } else if (donat.equals(secondDonat)) {
                    
                    for (String str: wilayah.keySet()) {
                        if (wilayah.get(str).suaraX < wilayah.get(str).suaraY) {
                            menang++;
                        }
                    }
                }
                out.println(menang);

            } else if (operasi.equals("CEK_SUARA_PROVINSI")) {
                
                for (String str: provinsi) {
                    out.println(str + " " + wilayah.get(str).suaraX + " " + wilayah.get(str).suaraY);
                }

            } else if (operasi.equals("WILAYAH_MINIMAL")) {

                String donat = in.next();
                double persentase = in.nextInt();
                long jumlahMenang = 0;

                for (String wil: wilayah.keySet()) {
                    double persentaseMenang;

                    if (wilayah.get(wil).suaraX == 0 && wilayah.get(wil).suaraY == 0) {
                        persentaseMenang = 50;
                    } else {
                        double suaraX = wilayah.get(wil).suaraX;
                        double suaraY = wilayah.get(wil).suaraY;
                        double total = suaraX + suaraY;

                        if (donat.equals(firstDonat)) {
                            persentaseMenang = (suaraX * 100) / total;
                        } else {
                            persentaseMenang = (suaraY * 100) / total;
                        }

                    }

                    if (persentaseMenang >= persentase) {
                        jumlahMenang++;
                    }
                }
                out.println(jumlahMenang);

            } else if (operasi.equals("WILAYAH_SELISIH")) {

                long minimal = in.nextLong();
                long selisih = 0;
                
                // for (String wil: wilayah.keySet()) {

                //     long suaraX = wilayah.get(wil).suaraX;
                //     long suaraY = wilayah.get(wil).suaraY;

                //     long persentaseSelisih = Math.abs(suaraX - suaraY);

                //     if (minimal <= persentaseSelisih) {
                //         selisih++;
                //     }

                // }
                // AVL_Tree.preOrder(root);

                selisih += AVL_Tree.getDenganSelisih(root, minimal);
                out.println(selisih);

            }
        }
        // AVL_Tree.preOrder(root);
        // System.out.println();
        // BTreePrinter.printNode(root);

        // for (String str: wilayah.keySet()) {
        //     out.println(str + " " + wilayah.get(str).suaraX + " " + wilayah.get(str).suaraY);
        // }

        out.close();
    }

    // taken from https://codeforces.com/submissions/Petr
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;
 
        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }
 
        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }
 
        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
 
    }
}

class BTreePrinter {

    public static <T extends Comparable<?>> void printNode(AVL_Node root) {
        int maxLevel = BTreePrinter.maxLevel(root);

        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }

    private static <T extends Comparable<?>> void printNodeInternal(List<AVL_Node> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || BTreePrinter.isAllElementsNull(nodes))
            return;

        int floor = maxLevel - level;
        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        BTreePrinter.printWhitespaces(firstSpaces);

        List<AVL_Node> newNodes = new ArrayList<AVL_Node>();
        for (AVL_Node node : nodes) {
            if (node != null) {
                System.out.print(node.key);
                newNodes.add(node.left);
                newNodes.add(node.right);
            } else {
                newNodes.add(null);
                newNodes.add(null);
                System.out.print(" ");
            }

            BTreePrinter.printWhitespaces(betweenSpaces);
        }
        System.out.println("");

        for (int i = 1; i <= endgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                BTreePrinter.printWhitespaces(firstSpaces - i);
                if (nodes.get(j) == null) {
                    BTreePrinter.printWhitespaces(endgeLines + endgeLines + i + 1);
                    continue;
                }

                if (nodes.get(j).left != null)
                    System.out.print("/");
                else
                    BTreePrinter.printWhitespaces(1);

                BTreePrinter.printWhitespaces(i + i - 1);

                if (nodes.get(j).right != null)
                    System.out.print("\\");
                else
                    BTreePrinter.printWhitespaces(1);

                BTreePrinter.printWhitespaces(endgeLines + endgeLines - i);
            }

            System.out.println("");
        }

        printNodeInternal(newNodes, level + 1, maxLevel);
    }

    private static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }

    private static <T extends Comparable<?>> int maxLevel(AVL_Node node) {
        if (node == null)
            return 0;

        return Math.max(BTreePrinter.maxLevel(node.left), BTreePrinter.maxLevel(node.right)) + 1;
    }

    private static <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null)
                return false;
        }

        return true;
    }

}