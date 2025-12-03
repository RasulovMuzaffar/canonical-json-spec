<?php

/**
 * Canonical JSON:
 *  - Object keys sorted lexicographically
 *  - Array element order preserved
 *  - Primitive values unchanged
 *  - Output is compact JSON without extra spaces/newlines
 *  - Unicode is not escaped
 */
class JsonCanonicalizer
{
    public static function canonicalJson(string $json): string
    {
        $data = json_decode($json, true, 512, JSON_THROW_ON_ERROR);
        $sorted = self::sortRecursively($data);

        // JSON_UNESCAPED_UNICODE — do not escape Unicode
        // JSON_UNESCAPED_SLASHES — cleaner URLs
        return json_encode($sorted, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    }

    private static function sortRecursively($value)
    {
        if (is_array($value)) {
            if (self::isAssoc($value)) {
                // associative array: treat as JSON object
                ksort($value, SORT_STRING);
                foreach ($value as $k => $v) {
                    $value[$k] = self::sortRecursively($v);
                }
                return $value;
            } else {
                // numeric array: JSON array, preserve order
                foreach ($value as $i => $v) {
                    $value[$i] = self::sortRecursively($v);
                }
                return $value;
            }
        }

        return $value;
    }

    private static function isAssoc(array $arr): bool
    {
        return array_keys($arr) !== range(0, count($arr) - 1);
    }
}
