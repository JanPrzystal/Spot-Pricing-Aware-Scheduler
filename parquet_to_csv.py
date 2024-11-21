import pandas as pd
import sys

file = sys.argv[1]

# read
df = pd.read_parquet(file)

df.to_csv(file.replace('.parquet', '.csv'))
