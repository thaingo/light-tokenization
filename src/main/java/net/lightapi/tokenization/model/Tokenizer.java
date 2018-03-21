
package net.lightapi.tokenization.model;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tokenizer {

    
    private String value;
    
    private Integer id;
    

    public Tokenizer () {
    }

    
    
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
    
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tokenizer tokenizer = (Tokenizer) o;

        return Objects.equals(value, tokenizer.value) &&
        
        Objects.equals(id, tokenizer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value,  id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Tokenizer {\n");
        
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
