package canonicaljson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Canonical JSON serializer:
 *  - Object fields are sorted lexicographically by key
 *  - Array element order is preserved
 *  - Primitive values (string, number, boolean, null) are kept as-is
 *  - Output is compact JSON without extra spaces/newlines
 */
public final class JsonCanonicalizer {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

  private JsonCanonicalizer() {
    // utility class
  }

  /**
   * Canonicalizes already parsed JsonNode.
   */
  public static String canonicalJson(JsonNode node) {
    JsonNode normalized = normalize(node);
    try {
      return MAPPER.writeValueAsString(normalized);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to canonicalize JSON", e);
    }
  }

  /**
   * Canonicalizes JSON string.
   */
  public static String canonicalJson(String json) {
    try {
      JsonNode node = MAPPER.readTree(json);
      return canonicalJson(node);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to parse JSON for canonicalization", e);
    }
  }

  private static JsonNode normalize(JsonNode node) {
    if (node.isObject()) {
      ObjectNode obj = FACTORY.objectNode();

      List<String> fieldNames = new ArrayList<>();
      node.fieldNames().forEachRemaining(fieldNames::add);
      Collections.sort(fieldNames);

      for (String field : fieldNames) {
        obj.set(field, normalize(node.get(field)));
      }
      return obj;

    } else if (node.isArray()) {
      ArrayNode arr = FACTORY.arrayNode();
      for (JsonNode child : node) {
        arr.add(normalize(child));
      }
      return arr;

    } else {
      return node;
    }
  }
}
