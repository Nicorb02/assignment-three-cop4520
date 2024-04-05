import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

class Present {
    int tag;
    Present next;

    public Present(int tag) {
        this.tag = tag;
        this.next = null;
    }
}

class LinkedPresents {
    private Present head;
    private Queue<Present> queue;
    private ReentrantLock lock;

    public LinkedPresents() {
        this.head = null;
        this.queue = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
    }

    public void addPresent(int tag) {
        Present newPresent = new Present(tag);
        try {
            lock.lock();
            if (head == null || tag < head.tag) {
                newPresent.next = head;
                head = newPresent;
            } else {
                Present current = head;
                Present prev = null;
                while (current != null && tag >= current.tag) {
                    prev = current;
                    current = current.next;
                }
                newPresent.next = current;
                prev.next = newPresent;
            }
        } finally {
            lock.unlock();
        }
        queue.add(newPresent);
    }

    public boolean removePresent(int tag) {
        try {
            lock.lock();
            if (head == null) return false;
            if (head.tag == tag) {
                head = head.next;
                return true;
            }
            Present current = head;
            Present prev = null;
            while (current != null && current.tag != tag) {
                prev = current;
                current = current.next;
            }
            if (current != null) {
                prev.next = current.next;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean containsPresent(int tag) {
        try {
            lock.lock();
            Present current = head;
            while (current != null) {
                if (current.tag == tag) {
                    return true;
                }
                current = current.next;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public int getSize() {
        return queue.size();
    }
}

public class MinotaurBirthdayParty {
    public static void main(String[] args) throws InterruptedException {
        LinkedPresents linkedPresents = new LinkedPresents();
        int totalPresents = 500000;

        Thread[] servants = new Thread[4];
        for (int i = 0; i < servants.length; i++) {
            servants[i] = new Thread(new Servant(linkedPresents, totalPresents));
            servants[i].start();
        }

        for (Thread servant : servants) {
            servant.join();
        }

        System.out.println("done");
    }
}

class Servant implements Runnable {
    private LinkedPresents linkedPresents;
    private int totalPresents;

    public Servant(LinkedPresents linkedPresents, int totalPresents) {
        this.linkedPresents = linkedPresents;
        this.totalPresents = totalPresents;
    }

    @Override
    public void run() {
        for (int i = 1; i <= totalPresents; i++) {
            int tag = (int) (Math.random() * totalPresents) + 1;
            // make threads do different tasks
            if (i % 2 == 0) {
                if (linkedPresents.removePresent(tag)) {
                    System.out.println("thanks " + tag);
                }
            } else {
                linkedPresents.addPresent(tag);
            }
        }
    }
}
