package za.co.topitupkeyboard.model;

/*
 * Created by manoj on 04-Jan-18
 * Mobile 0782222143
 * Email manojecdvg@gmail.com/
*/

public class ItemSPI {

    public String text;
    public String deno;
    public int drawable;
    public String color;
    public int pos;
    public String bmenu;
    public String item_barcode;

    public boolean visible_cashier;
    public boolean visible_admin;


    public ItemSPI(String text, int pos, int drawable, String color, String bmenu, String item_barcode, boolean visible_admin, boolean visible_cashier,String deno ) {
        this.text = text;
        this.drawable = drawable;
        this.color = color;
        this.pos=pos;
        this.bmenu=bmenu;
        this.item_barcode=item_barcode;
        this.visible_cashier=visible_cashier;
        this.visible_admin=visible_admin;
        this.deno=deno;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getBmenu() {
        return bmenu;
    }

    public void setBmenu(String bmenu) {
        this.bmenu = bmenu;
    }

    public String getItem_barcode() {
        return item_barcode;
    }

    public void setItem_barcode(String item_barcode) {
        this.item_barcode = item_barcode;
    }

    public void setVisible_cashier(boolean visible_cashier) {
        this.visible_cashier = visible_cashier;
    }

    public void setVisible_admin(boolean visible_admin) {
        this.visible_admin = visible_admin;
    }

    public String getDeno() {
        return deno;
    }

    public void setDeno(String deno) {
        this.deno = deno;
    }
}
