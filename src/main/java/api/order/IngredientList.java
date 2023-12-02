package api.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IngredientList {

    @JsonProperty("data")
    private List<IngredientData> data;

    @JsonProperty("success")
    private boolean success;

    public List<IngredientData> getData() {
        return data;
    }

    public void setData(List<IngredientData> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
