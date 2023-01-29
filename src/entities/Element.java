package entities;

public record Element(int[] features) {

    public Element clone() {
        return new Element(features);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;
        Element element = (Element) obj;

        for (int i = 0; i < features.length; i++) {
            if (this.features[i] != element.features()[i]) {
                result = false;
                break;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Element(");

        sb.append(features[0]);

        for (int i = 1; i < features.length; i++) {
            sb.append(", ").append(features[i]);
        }

        sb.append(')');

        return sb.toString();
    }
}
