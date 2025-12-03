package canonicaljson

import (
	"bytes"
	"encoding/json"
	"sort"
)

// CanonicalJSON takes raw JSON bytes and returns canonical JSON bytes.
//
// Rules:
//  - Object keys are sorted lexicographically
//  - Array element order is preserved
//  - Primitive values are unchanged
//  - Output is compact JSON without extra spaces/newlines
func CanonicalJSON(input []byte) ([]byte, error) {
	var v any
	if err := json.Unmarshal(input, &v); err != nil {
		return nil, err
	}

	var buf bytes.Buffer
	if err := encodeCanonical(&buf, v); err != nil {
		return nil, err
	}
	return buf.Bytes(), nil
}

func encodeCanonical(buf *bytes.Buffer, v any) error {
	switch val := v.(type) {
	case map[string]any:
		keys := make([]string, 0, len(val))
		for k := range val {
			keys = append(keys, k)
		}
		sort.Strings(keys)

		buf.WriteByte('{')
		for i, k := range keys {
			if i > 0 {
				buf.WriteByte(',')
			}
			keyBytes, _ := json.Marshal(k)
			buf.Write(keyBytes)
			buf.WriteByte(':')

			if err := encodeCanonical(buf, val[k]); err != nil {
				return err
			}
		}
		buf.WriteByte('}')

	case []any:
		buf.WriteByte('[')
		for i, elem := range val {
			if i > 0 {
				buf.WriteByte(',')
			}
			if err := encodeCanonical(buf, elem); err != nil {
				return err
			}
		}
		buf.WriteByte(']')

	default:
		b, err := json.Marshal(val)
		if err != nil {
			return err
		}
		buf.Write(b)
	}
	return nil
}
