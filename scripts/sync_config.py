"""
Reads every JSON file in config/ and upserts it into the MongoDB Config collection.
Matches on the `type` field (GAME, ABILITIES, CLASSES, MOBS, SPEC_BOOSTS).

Required env var:
  MONGO_URI  - MongoDB SRV connection string (same value as keys.yml `database_key`)

Usage:
  pip install pymongo
  MONGO_URI="mongodb+srv://..." python scripts/sync_config.py
"""

import os
import sys
from pathlib import Path

from bson import json_util
from pymongo import MongoClient

MONGO_URI = os.environ.get("MONGO_URI")
if not MONGO_URI:
    print("ERROR: MONGO_URI environment variable is not set.", file=sys.stderr)
    sys.exit(1)

CONFIG_DIR = Path(__file__).parent.parent / "config"
if not CONFIG_DIR.is_dir():
    print(f"ERROR: config directory not found at {CONFIG_DIR}", file=sys.stderr)
    sys.exit(1)

client = MongoClient(MONGO_URI)
collection = client["Warlords"]["Config"]

json_files = sorted(CONFIG_DIR.glob("*.json"))
if not json_files:
    print("No JSON files found in config/")
    sys.exit(0)

errors = 0
for json_file in json_files:
    try:
        doc = json_util.loads(json_file.read_text(encoding="utf-8"))
        config_type = doc.get("type")
        if not config_type:
            print(f"SKIP {json_file.name}: missing 'type' field")
            continue
        result = collection.replace_one({"type": config_type}, doc, upsert=True)
        status = f"upserted={result.upserted_id}" if result.upserted_id else f"modified={result.modified_count}"
        print(f"OK   {json_file.name} ({config_type}): matched={result.matched_count} {status}")
    except Exception as e:
        print(f"FAIL {json_file.name}: {e}", file=sys.stderr)
        errors += 1

client.close()

if errors:
    print(f"\n{errors} file(s) failed.", file=sys.stderr)
    sys.exit(1)

print(f"\nDone. {len(json_files) - errors}/{len(json_files)} configs synced.")
