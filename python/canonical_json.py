import json
from typing import Any


def _sort_recursively(value: Any) -> Any:
    if isinstance(value, dict):
        # object: sort keys lexicographically
        return {k: _sort_recursively(value[k]) for k in sorted(value.keys())}
    elif isinstance(value, list):
        # array: preserve order
        return [_sort_recursively(v) for v in value]
    else:
        # primitive
        return value


def canonical_json(s: str) -> str:
    """
    Canonical JSON:
      - Object keys sorted lexicographically
      - Array order preserved
      - Primitive values unchanged
      - Output is compact JSON without spaces/newlines
      - Unicode is not escaped
    """
    obj = json.loads(s)
    sorted_obj = _sort_recursively(obj)
    return json.dumps(sorted_obj, separators=(',', ':'), ensure_ascii=False)
