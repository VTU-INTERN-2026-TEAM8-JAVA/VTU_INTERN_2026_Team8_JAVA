import Header from "@/components/Header";
import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "WealthWise",
  description: "Mutual fund portfolio, goal planning, and investor analytics.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
  <html lang="en">
    <body>
     <Header/>
      {children}

    </body>
  </html>
);
}