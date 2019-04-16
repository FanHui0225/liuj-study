package com.stereo.study.rpc.utils;

/**
 * Created by liujing11 on 2015/6/19.
 */
public final class RedBlackTree {

    private RedBlackNode root;

    public RedBlackTree() {
        root = RedBlackNode.NILL;
    }

    public RedBlackTree(Comparable element) {
        root = new RedBlackNode(element, RedBlackNode.BLACK);
        root.parent = RedBlackNode.NILL;
    }

    public void insert(Comparable element) {
        insert(new RedBlackNode(element, RedBlackNode.RED));
    }

    private void insert(RedBlackNode node) {
        RedBlackNode preNode = RedBlackNode.NILL;
        RedBlackNode curNode = root;
        while (curNode != RedBlackNode.NILL) {
            preNode = curNode;
            if (node.element.compareTo(curNode.element) < 0) {
                curNode = curNode.left;
            } else if (node.element.compareTo(curNode.element) > 0) {
                curNode = curNode.right;
            } else {
                return;
            }
        }

        node.parent = preNode;
        if (preNode == RedBlackNode.NILL) {
            root = node;
        } else if (node.element.compareTo(preNode.element) < 0) {
            preNode.left = node;
        } else {
            preNode.right = node;
        }
        node.left = node.right = RedBlackNode.NILL;
        node.color = RedBlackNode.RED;
        BrInsertFixup(node);

    }

    private void BrInsertFixup(RedBlackNode node) {
        RedBlackNode pNode;
        while (node.parent.color == RedBlackNode.RED) {
            if (node.parent == node.parent.parent.left) {
                pNode = node.parent.parent.right;
                if (pNode.color == RedBlackNode.RED) {
                    node.parent.color = RedBlackNode.BLACK;
                    pNode.color = RedBlackNode.BLACK;
                    node.parent.parent.color = RedBlackNode.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        leftRotate(node);
                    }
                    node.parent.color = RedBlackNode.BLACK;
                    node.parent.parent.color = RedBlackNode.RED;
                    rightRotate(node.parent.parent);
                    // node = node.parent;
                }
            } else {
                pNode = node.parent.parent.left;
                if (pNode.color == RedBlackNode.RED) {
                    node.parent.color = RedBlackNode.BLACK;
                    pNode.color = RedBlackNode.BLACK;
                    node.parent.parent.color = RedBlackNode.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rightRotate(node);
                    }
                    node.parent.color = RedBlackNode.BLACK;
                    node.parent.parent.color = RedBlackNode.RED;
                    leftRotate(node.parent.parent);
                    // node = node.parent;
                }
            }
        }
        root.color = RedBlackNode.BLACK;

    }

    private void leftRotate(RedBlackNode node) {
        if (node == null || node == RedBlackNode.NILL)
            return;
        RedBlackNode pNode = node.right;
        node.right = pNode.left;
        pNode.left.parent = node;
        pNode.parent = node.parent;
        if (node.parent == RedBlackNode.NILL) {
            root = pNode;
        } else if (node == node.parent.left) {
            node.parent.left = pNode;
        } else {
            node.parent.right = pNode;
        }
        pNode.left = node;
        node.parent = pNode;

    }

    private void rightRotate(RedBlackNode node) {
        if (node == null || node == RedBlackNode.NILL)
            return;
        RedBlackNode pNode = node.left;
        node.left = pNode.right;
        pNode.right.parent = node;
        pNode.parent = node.parent;
        if (node.parent == RedBlackNode.NILL) {
            root = pNode;
        } else if (node == node.parent.left) {
            node.parent.left = pNode;
        } else {
            node.parent.right = pNode;
        }
        pNode.right = node;
        node.parent = pNode;
    }

    private RedBlackNode findTreeMiniMUM(RedBlackNode node) {
        if (node == null || node == RedBlackNode.NILL)
            return null;
        while (node.left != RedBlackNode.NILL) {
            node = node.left;
        }

        return node;
    }

    private RedBlackNode findSuccessor(RedBlackNode node) {
        if (node == null || node == RedBlackNode.NILL) {
            return RedBlackNode.NILL;
        }

        if (node.right != RedBlackNode.NILL) {
            return findTreeMiniMUM(node.right);
        }

        RedBlackNode pNode = node.parent;
        while (pNode != RedBlackNode.NILL && node == pNode.right) {
            node = pNode;
            pNode = pNode.parent;
        }
        return pNode;
    }

    private RedBlackNode delete(RedBlackNode node) {
        if (node == null || node == RedBlackNode.NILL) {
            return RedBlackNode.NILL;
        }

        RedBlackNode pNode;

        if (node.left == RedBlackNode.NILL || node.right == RedBlackNode.NILL) {
            pNode = node;
        } else {
            pNode = findSuccessor(node);
        }

        RedBlackNode nNode;
        if (pNode.left != RedBlackNode.NILL) {
            nNode = pNode.left;
        } else {
            nNode = pNode.right;
        }

        nNode.parent = pNode.parent;
        if (pNode.parent == RedBlackNode.NILL) {
            root = nNode;
        } else {
            if (pNode == pNode.parent.left) {
                pNode.parent.left = nNode;
            } else {
                pNode.parent.right = nNode;
            }
        }

        if (pNode != node) {
            node.element = pNode.element;
        }

        if (pNode.color == RedBlackNode.BLACK) {
            BrDeleteFixup(nNode);
        }

        return pNode;
    }

    private void BrDeleteFixup(RedBlackNode node) {
        if (node == null) {
            return;
        }
        while (node != root && node.color == RedBlackNode.BLACK) {
            if (node == node.parent.left) {
                RedBlackNode bNode = node.parent.right;
                if (bNode.color == RedBlackNode.RED) {
                    bNode.color = RedBlackNode.BLACK;
                    node.parent.color = RedBlackNode.RED;
                    leftRotate(node.parent);
                    bNode = node.parent.right;
                }
                if (bNode.left.color == RedBlackNode.BLACK
                        && bNode.right.color == RedBlackNode.BLACK) {
                    bNode.color = RedBlackNode.RED;
                    node = node.parent;
                    continue;
                } else if (bNode.right.color == RedBlackNode.BLACK) {
                    bNode.left.color = RedBlackNode.BLACK;
                    bNode.color = RedBlackNode.RED;
                    rightRotate(bNode);
                    bNode = node.parent.left;
                }
                bNode.color = node.parent.color;
                node.parent.color = RedBlackNode.BLACK;
                bNode.right.color = RedBlackNode.BLACK;
                leftRotate(node.parent);
                node = root;

            } else {
                RedBlackNode bNode = node.parent.left;
                if (bNode.color == RedBlackNode.RED) {
                    bNode.color = RedBlackNode.BLACK;
                    node.parent.color = RedBlackNode.RED;
                    rightRotate(node.parent);
                    bNode = node.parent.left;
                }
                if (bNode.left.color == RedBlackNode.BLACK
                        && bNode.right.color == RedBlackNode.BLACK) {
                    bNode.color = RedBlackNode.RED;
                    node = node.parent;
                    continue;
                } else if (bNode.left.color == RedBlackNode.BLACK) {
                    bNode.right.color = RedBlackNode.BLACK;
                    bNode.color = RedBlackNode.RED;
                    leftRotate(bNode);
                    bNode = node.parent.right;
                }
                bNode.color = node.parent.color;
                node.parent.color = RedBlackNode.BLACK;
                bNode.left.color = RedBlackNode.BLACK;
                rightRotate(node.parent);
                node = root;
            }
        }
        node.color = RedBlackNode.BLACK;

    }

    public Comparable delete(Comparable element) {
        if (element == null) {
            return null;
        }
        RedBlackNode node = findBRNode(element);
        if (node == null || node == RedBlackNode.NILL) {
            return null;
        }
        return delete(node).element;
    }

    private RedBlackNode findBRNode(Comparable element) {
        if (element == null) {
            return RedBlackNode.NILL;
        }

        RedBlackNode node = root;
        while (node != RedBlackNode.NILL) {
            if (node.element.compareTo(element) == 0) {
                break;
            } else if (node.element.compareTo(element) > 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node;
    }

    public static class RedBlackNode {
        public static final byte RED = 0x00;
        public static final byte BLACK = 0x01;
        public static RedBlackNode NILL = new RedBlackNode(BLACK);
        RedBlackNode parent;
        RedBlackNode left;
        RedBlackNode right;
        Comparable element;
        byte color;

        public RedBlackNode(RedBlackNode left, RedBlackNode right,
                            Comparable element) {
            this.left = left;
            this.right = right;
            this.element = element;
            color = RED;
        }

        public RedBlackNode(byte color) {
            this.color = color;
        }

        public RedBlackNode(Comparable element, byte color) {
            this.color = color;
            this.element = element;
            this.left = NILL;
            this.right = NILL;
        }
    }
}