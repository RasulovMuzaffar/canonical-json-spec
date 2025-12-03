using System;
using System.Linq;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace CanonicalJson
{
    /// <summary>
    /// Canonical JSON:
    ///  - Object properties sorted lexicographically by name
    ///  - Array order preserved
    ///  - Primitive values unchanged
    ///  - Output is compact JSON (no extra spaces/newlines)
    /// </summary>
    public static class JsonCanonicalizer
    {
        public static string CanonicalJson(string json)
        {
            var token = JToken.Parse(json);
            var sorted = SortRecursively(token);
            return JsonConvert.SerializeObject(sorted, Formatting.None);
        }

        private static JToken SortRecursively(JToken token)
        {
            switch (token.Type)
            {
                case JTokenType.Object:
                    var obj = (JObject)token;
                    var sortedObj = new JObject(
                        obj.Properties()
                           .OrderBy(p => p.Name, StringComparer.Ordinal)
                           .Select(p => new JProperty(p.Name, SortRecursively(p.Value)))
                    );
                    return sortedObj;

                case JTokenType.Array:
                    var arr = (JArray)token;
                    var newArr = new JArray();
                    foreach (var item in arr)
                    {
                        newArr.Add(SortRecursively(item));
                    }
                    return newArr;

                default:
                    return token;
            }
        }
    }
}
