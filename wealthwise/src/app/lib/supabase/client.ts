import { createBrowserClient } from "@supabase/ssr";

export function createClient() {
  return createBrowserClient(
    process.env.https://exkqbxsefffkclseoxwa.supabase.co!,
    process.env.eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV4a3FieHNlZmZma2Nsc2VveHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzMyNDg2MjQsImV4cCI6MjA4ODgyNDYyNH0.fIwXub1sh1X9648KEDa8W9j5PnbiWbzq0OhnhP0kU7w!
  );
}